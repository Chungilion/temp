import tkinter as tk
from tkinter import filedialog, messagebox
from PIL import Image, ImageTk
import sqlite3
import torch
import torchvision.transforms as transforms
import model  # Import model.py, which now saves the trained model to MODEL_PATH
import os

class App:
    def __init__(self, root):
        self.root = root
        self.root.title("CIFAR-10 Image Classifier")
        self.root.geometry("300x200")  # Initial small size for the training screen

        # Database connection setup
        self.conn = sqlite3.connect('predictions.db')
        self.create_database()  # Ensure the predictions table is created

        # Initialize frames
        self.training_frame = tk.Frame(self.root, bg="#f0f0f0")
        self.prediction_frame = tk.Frame(self.root, bg="#f0f0f0")

        # Training frame widgets
        self.train_button = tk.Button(
            self.training_frame, text="Start Training", command=self.start_training, 
            bg="#4CAF50", fg="white", font=("Helvetica", 12, "bold"), width=20
        )
        self.train_button.pack(pady=20)

        self.switch_to_prediction_button = tk.Button(
            self.training_frame, text="Go to Prediction Screen", 
            command=lambda: self.show_frame(self.prediction_frame), 
            bg="#2196F3", fg="white", font=("Helvetica", 12, "bold"), width=20
        )
        self.switch_to_prediction_button.pack(pady=10)

        # Prediction frame widgets
        self.upload_button = tk.Button(
            self.prediction_frame, text="Upload Image and Predict", 
            command=self.predict_image, bg="#4CAF50", fg="white", font=("Helvetica", 12, "bold"), width=20
        )
        self.upload_button.pack(pady=20)

        self.image_label = tk.Label(self.prediction_frame, bg="#f0f0f0", borderwidth=2, relief="solid")
        self.image_label.pack(pady=10)

        self.result_label = tk.Label(self.prediction_frame, text="", font=("Helvetica", 16), fg="#333333", bg="#f0f0f0")
        self.result_label.pack(pady=10)

        self.switch_to_training_button = tk.Button(
            self.prediction_frame, text="Go Back to Training Screen", 
            command=lambda: self.show_frame(self.training_frame), 
            bg="#2196F3", fg="white", font=("Helvetica", 12, "bold"), width=20
        )
        self.switch_to_training_button.pack(pady=20)

        # Show initial frame
        self.show_frame(self.training_frame)

    def create_database(self):
        """Creates the predictions table if it does not exist."""
        cursor = self.conn.cursor()
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS predictions (
                id INTEGER PRIMARY KEY,
                image_path TEXT,
                prediction TEXT
            )
        ''')
        self.conn.commit()

    def show_frame(self, frame):
        # Hide both frames first
        self.training_frame.pack_forget()
        self.prediction_frame.pack_forget()

        # Resize window based on frame
        if frame == self.training_frame:
            self.root.geometry("300x200")  # Smaller size for the training screen
        else:
            self.root.geometry("400x600")  # Larger size for the prediction screen

        # Show the selected frame
        frame.pack(expand=True, fill="both")

    def start_training(self):
        """Run training directly without threading."""
        messagebox.showinfo("Training", "Training started... This may take a while.")
        model.train_model()  # Calls the train_model function from model.py
        messagebox.showinfo("Training", "Training complete.")

    def predict_image(self):
        """Prediction logic for the uploaded image."""
        if not hasattr(model, 'Net'):
            messagebox.showwarning("Model Not Loaded", "Please train the model first.")
            return

        # Load the saved model
        net = model.Net()
        if os.path.exists(model.MODEL_PATH):
            net.load_state_dict(torch.load(model.MODEL_PATH))
            net.eval()
        else:
            messagebox.showwarning("Model Not Found", "Please train and save the model first.")
            return

        # File dialog to select an image
        file_path = filedialog.askopenfilename(filetypes=[("Image files", "*.jpg;*.jpeg;*.png")])
        if file_path:
            # Ensure the 'uploaded_images' folder exists
            save_folder = "uploaded_images"
            os.makedirs(save_folder, exist_ok=True)

            # Save the uploaded image in 'uploaded_images' with the original filename
            save_path = os.path.join(save_folder, os.path.basename(file_path))
            image = Image.open(file_path)
            image.save(save_path)

            # Display the uploaded image in the UI
            image = image.resize((150, 150), Image.ANTIALIAS)  # Resize the image to fit in the UI frame
            img_display = ImageTk.PhotoImage(image)
            self.image_label.config(image=img_display)
            self.image_label.image = img_display

            # Preprocess the image and predict
            transform = transforms.Compose([
                transforms.Resize((32, 32)),
                transforms.ToTensor(),
                transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5)),
            ])
            image_tensor = transform(image).unsqueeze(0)

            with torch.no_grad():
                outputs = net(image_tensor)
                _, predicted = torch.max(outputs.data, 1)
                label = model.classes[predicted.item()]  # Assuming `classes` is defined in model.py
            
            self.result_label.config(text=f"Predicted Label: {label}")
            
            # Save the prediction to the database, using the new saved image path
            self.save_prediction(save_path, label)

    def save_prediction(self, image_path, prediction):
        """Save the prediction result to the database."""
        cursor = self.conn.cursor()
        cursor.execute("INSERT INTO predictions (image_path, prediction) VALUES (?, ?)", (image_path, prediction))
        self.conn.commit()
        messagebox.showinfo("Prediction Saved", "Image and prediction saved to database.")

    def __del__(self):
        """Ensure the database connection is closed when the app is closed."""
        self.conn.close()

# Ensure this is the only entry point
if __name__ == "__main__":
    root = tk.Tk()
    app = App(root)
    root.mainloop()
