from sklearn import svm, preprocessing
import numpy as np


class SVM:

    def __init__(self, gamma_val, C_val):
        self.classifier = svm.SVC(gamma=gamma_val, C=C_val)

    def train(self, tdata, tlabels):
        print("Training SVM classifier...")
        data, labels = np.array(tdata.contents), np.array(tlabels.contents)
        data = preprocessing.scale(data)
        self.classifier.fit(data, labels)
        print(self.classifier.n_support_)
        print(self.classifier)

    def test(self, tdata, tlabels):
        print("Testing SVM classifier...")
        errors = 0
        test_data = preprocessing.scale(np.array(tdata.contents))
        guesses = self.classifier.predict(test_data)
        print(np.histogram(guesses))
        for i in range(len(guesses)):
            if guesses[i] != tlabels.contents[i]:
                errors += 1
        print("Testing complete: {0} mistakes were made.".format(errors))
