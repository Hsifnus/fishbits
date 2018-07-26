import file_parser as fp
from mira_perceptron import Perceptron
from ada_boost import AdaBoost
from svm import SVM

if __name__ == '__main__':

    print("Parsing training data...")
    train_labels = fp.parse("train-labels.idx1-ubyte", "label")
    train_data = fp.parse("train-images.idx3-ubyte", "image")

    poss_labels = [x for x in range(10)]
    # perceptron = Perceptron(poss_labels, len(train_data.contents[0]))
    # perceptron.train(train_data, train_labels)
    # ada = AdaBoost(train_data, train_labels, 10)
    # ada.train()
    svm = SVM(0.0001, 800)
    svm.train(train_data, train_labels)

    print("Parsing test data...")
    test_labels = fp.parse("t10k-labels.idx1-ubyte", "label")
    test_data = fp.parse("t10k-images.idx3-ubyte", "image")

    print("First 5 labels:")
    for i in range(5):
        print(test_labels.contents[i])
    print("First 5 images:")
    for i in range(5):
        print(test_data.contents[i][16*28:17*28], "...")

    # perceptron.test(test_data, test_labels)
    # ada.test(test_data, test_labels)
    svm.test(test_data, test_labels)

    print("Test succeeded!")
