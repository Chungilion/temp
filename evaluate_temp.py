import numpy as np
from keras.models import load_model
from sklearn.metrics import accuracy_score, classification_report
from sklearn.svm import SVC
from skimage import feature
import joblib
import cv2
from keras.datasets import cifar10
from tqdm import tqdm

# Danh sách tên các lớp trong CIFAR-10
class_names = ['airplane', 'automobile', 'bird', 'cat', 'deer', 'dog', 'frog', 'horse', 'ship', 'truck']

# 1. Load CIFAR-10 dataset
(x_train, y_train), (x_test, y_test) = cifar10.load_data()
y_test = y_test.astype(np.int64)  # Convert to int64 for compatibility

# 2. Load mô hình CNN và SVM
def load_models():
    cnn_model = load_model("cnn_model.h5")
    svm_model = joblib.load("svm_model.pkl")
    return cnn_model, svm_model

# 3. Trích xuất đặc trưng HOG
def extract_hog_features(images):
    hog_features = []
    for img in tqdm(images, desc="Extracting HOG features"):
        img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)  # Chuyển sang ảnh xám
        img = cv2.resize(img, (64, 128))  # Thay đổi kích thước
        features = feature.hog(img, block_norm='L2-Hys', visualize=False)
        hog_features.append(features)
    return np.array(hog_features)

# 4. Đánh giá mô hình CNN
def evaluate_cnn(cnn_model, x_test, y_test):
    loss, accuracy = cnn_model.evaluate(x_test, y_test, verbose=0)
    print(f"CNN Model Accuracy: {accuracy * 100:.2f}%")

# 5. Đánh giá mô hình SVM
def evaluate_svm(svm_model, x_test, y_test):
    x_test_hog = extract_hog_features(x_test)
    predictions = svm_model.predict(x_test_hog)
    accuracy = accuracy_score(y_test, predictions)
    print(f"SVM Model Accuracy: {accuracy * 100:.2f}%")
    print("Classification Report:")
    print(classification_report(y_test, predictions, target_names=class_names))

if __name__ == '__main__':
    # Load models
    cnn_model, svm_model = load_models()

    # Evaluate CNN model
    evaluate_cnn(cnn_model, x_test, y_test)

    # Evaluate SVM model
    evaluate_svm(svm_model, x_test, y_test)
