import random, numpy as np


class Perceptron:

    def __init__(self, labels, vector_size):
        print("Initializing MIRA perceptron")
        self.labels = labels
        self.wvectors = {}
        self.vsize = vector_size
        # initialize multi-class weight vectors with random weights
        for label in labels:
            self.wvectors[label] = [random.uniform(-0.7, 0.7) for _ in range(self.vsize)]

    def train(self, data, tlabels):
        goal = 10000
        errors = 10010
        rounds, limit = 0, 3
        while errors > goal and rounds < limit:
            print("Starting a round of training...")
            errors = 0
            for i in range(data.max_size):
                guess = self.labels[np.argmin([np.dot(self.wvectors[label], data.contents[i]) for label in self.labels])]
                correct = tlabels.contents[i]
                if guess != correct: # Mistake was made
                    # print("Expected label {0} but got label {1} instead.".format(correct, guess))
                    errors += 1
                    tau = np.dot(np.subtract(self.wvectors[guess], self.wvectors[correct]), data.contents[i]) + 1
                    tau = tau / (2 * np.dot(data.contents[i], data.contents[i]))
                    diff = np.dot(tau, data.contents[i])
                    self.wvectors[guess] = np.subtract(self.wvectors[guess], diff)
                    self.wvectors[correct] = np.add(self.wvectors[correct], diff)
            print("Round complete: {0} mistakes were made.".format(errors))
            rounds += 1
        print("Training complete!")

    def test(self, data, tlabels):
        print("Starting a round of testing...")
        errors = 0
        for i in range(data.max_size):
            guess = self.labels[np.argmin([np.dot(self.wvectors[label], data.contents[i]) for label in self.labels])]
            correct = tlabels.contents[i]
            if guess != correct:
                errors += 1
        print("Round complete: {0} mistakes were made.".format(errors))