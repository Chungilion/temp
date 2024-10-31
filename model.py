import torch
import torch.nn as nn
import torch.optim as optim
import torchvision
import torchvision.transforms as transforms
import torch.nn.functional as F
import numpy as np
from sklearn import svm
from sklearn.metrics import accuracy_score
from tqdm import tqdm  # Import tqdm for progress bar

# Define path to save and load the model
MODEL_PATH = "./cifar_net.pth"

# Add this at the top of model.py
classes = ['plane', 'car', 'bird', 'cat', 'deer', 'dog', 'frog', 'horse', 'ship', 'truck']

# Define a simple CNN model
class Net(nn.Module):
    def __init__(self):
        super(Net, self).__init__()
        self.conv1 = nn.Conv2d(3, 16, 3, padding=1)
        self.conv2 = nn.Conv2d(16, 32, 3, padding=1)
        self.pool = nn.MaxPool2d(2, 2)
        self.fc1 = nn.Linear(32 * 8 * 8, 128)
        self.fc2 = nn.Linear(128, 10)
    
    def forward(self, x):
        x = self.pool(F.relu(self.conv1(x)))
        x = self.pool(F.relu(self.conv2(x)))
        x = x.view(-1, 32 * 8 * 8)
        x = F.relu(self.fc1(x))
        x = self.fc2(x)
        return x

# Global stop flag for training
stop_training = False

def set_stop_training(value):
    global stop_training
    stop_training = value

def extract_features(net, dataloader):
    net.eval()
    features = []
    labels = []
    with torch.no_grad():
        for data in dataloader:
            inputs, targets = data
            x = net.pool(F.relu(net.conv1(inputs)))
            x = net.pool(F.relu(net.conv2(x)))
            x = x.view(-1, 32 * 8 * 8)
            x = F.relu(net.fc1(x))
            features.append(x.cpu().numpy())
            labels.append(targets.cpu().numpy())
    features = np.concatenate(features, axis=0)
    labels = np.concatenate(labels, axis=0)
    return features, labels

def train_model():
    global stop_training
    # Load and normalize the CIFAR10 dataset
    transform = transforms.Compose([
        transforms.ToTensor(),
        transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5)),
    ])

    trainset = torchvision.datasets.CIFAR10(root='./data', train=True, download=True, transform=transform)
    trainloader = torch.utils.data.DataLoader(trainset, batch_size=64, shuffle=True, num_workers=2)

    testset = torchvision.datasets.CIFAR10(root='./data', train=False, download=True, transform=transform)
    testloader = torch.utils.data.DataLoader(testset, batch_size=64, shuffle=False, num_workers=2)

    # Initialize model, loss function, and optimizer
    net = Net()
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.SGD(net.parameters(), lr=0.001, momentum=0.9)

    # Training loop with progress bar and stop condition
    for epoch in range(20):  # Train for 20 epochs
        if stop_training:
            print("Training stopped.")
            return net  # Exit if stop flag is set
        net.train()
        running_loss = 0.0
        progress_bar = tqdm(enumerate(trainloader, 0), total=len(trainloader), desc=f"Epoch {epoch+1}/20", unit="batch")
        
        for i, data in progress_bar:
            if stop_training:
                print("Training stopped.")
                return net  # Exit if stop flag is set
            inputs, labels = data
            optimizer.zero_grad()
            outputs = net(inputs)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()
            running_loss += loss.item()
            progress_bar.set_postfix(loss=running_loss / (i + 1))

    print("Finished Training")

    # Save the trained model to disk
    torch.save(net.state_dict(), MODEL_PATH)
    print(f"Model saved to {MODEL_PATH}")

    # Extract features using the trained CNN
    train_features, train_labels = extract_features(net, trainloader)
    test_features, test_labels = extract_features(net, testloader)

    # Train SVM on the extracted features
    clf = svm.SVC(kernel='linear')
    clf.fit(train_features, train_labels)

    # Predict and evaluate on test set
    test_predictions = clf.predict(test_features)
    accuracy = accuracy_score(test_labels, test_predictions)
    print(f"SVM Test Accuracy: {accuracy * 100:.2f}%")

    return net
