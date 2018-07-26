from file_parser import DataCollection
import math


class AdaBoost:

    def __init__(self, tdata, tlabels, nlabels):

        def classifier(index):

            def classify(value):

                pixel = self.pixel_hist.contents[index]

                # Round to nearest value that exists in pixel histogram
                min_diff, rounded_value = -1, -1
                for key in pixel.keys():
                    diff = abs(value - key)
                    if diff < min_diff or min_diff == -1:
                        min_diff = diff
                        rounded_value = key

                # Find mode of pixel-value-to-label histogram
                result, curr_max = None, -100
                for key in pixel[rounded_value].keys():
                    if key == "total":
                        continue
                    if pixel[rounded_value][key] > curr_max or not result:
                        curr_max = pixel[rounded_value][key]
                        result = key

                return result

            return classify

        print("Setting up AdaBoost algorithm with pixel-based classifiers...")

        self.samples = 60000
        self.pixel_hist = DataCollection(len(tdata.contents[0]), tdata.magic)
        self.data = tdata
        self.labels = tlabels
        self.nlabels = nlabels
        self.classifiers = [classifier(i) for i in range(self.pixel_hist.max_size)]
        self.ob_weights = [1 / self.samples for _ in range(self.samples)]
        self.alphas = []

        for _ in range(self.pixel_hist.max_size):
            self.pixel_hist.append({})

        for k in range(self.samples):
            if k % 1000 == 0:
                print(k)
            image = self.data.contents[k]
            for i in range(len(image)):
                if image[i] not in self.pixel_hist.contents[i].keys():
                    self.pixel_hist.contents[i][image[i]] = {"total": 0}
                if tlabels.contents[k] not in self.pixel_hist.contents[i][image[i]].keys():
                    self.pixel_hist.contents[i][image[i]][tlabels.contents[k]] = 1
                else:
                    self.pixel_hist.contents[i][image[i]][tlabels.contents[k]] += 1
                self.pixel_hist.contents[i][image[i]]["total"] += 1


    def round_value(self, index, value):
        pixel = self.pixel_hist.contents[index]

        # Round to nearest value that exists in pixel histogram
        min_diff, rounded_value = -1, -1
        for key in pixel.keys():
            diff = abs(value - key)
            if diff < min_diff or min_diff == -1:
                min_diff = diff
                rounded_value = key
        return rounded_value

    def train(self):

        print("Training AdaBoost algorithm...")
        image_len = len(self.data.contents[0])
        for _ in range(3):
            for i in range(image_len):

                guesses = [self.classifiers[i](self.data.contents[j][i]) for j in range(self.samples)]
                mismatches = [n for n in range(self.samples) if guesses[n] != self.labels.contents[n]]
                matches = [n for n in range(self.samples) if guesses[n] == self.labels.contents[n]]
                weighted_error = sum([self.ob_weights[n] for n in mismatches]) / \
                                 sum([self.ob_weights[n] for n in range(self.samples)])

                print("weighted_error: {0}".format(weighted_error))
                alpha = math.log(((self.nlabels - 1) * (1 - weighted_error)) / weighted_error)

                # # reweigh misclassifiers and normalize weights
                # for n in range(self.samples):
                #     # local_hist = self.pixel_hist.contents[i][self.data.contents[n][i]]
                #     # scalar = (local_hist[guesses[n]] - local_hist[self.labels.contents[n]]) / local_hist[guesses[n]]
                #     if n in mismatches:
                #         self.ob_weights[n] *= math.exp(alpha)

                for n in mismatches:
                    local_hist = self.pixel_hist.contents[i][self.data.contents[n][i]]
                    scalar = (local_hist[guesses[n]] - local_hist[self.labels.contents[n]]) / local_hist[guesses[n]]
                    self.ob_weights[n] *= math.exp(alpha * scalar)
                for n in matches:
                    self.ob_weights[n] = 1 / self.samples
                norm = sum(self.ob_weights)
                self.ob_weights = [w / norm for w in self.ob_weights]
                self.alphas.append(alpha)
                print("Round {0} complete: {1} mismatches made".format(i, len(mismatches)))

        print("Training complete: alphas are: {0}".format(self.alphas))


    def test(self, tdata, tlabels):

        print("Testing AdaBoost...")
        errors = 0
        for i in range(tdata.max_size):
            weighted_hist = {}

            for j in range(self.nlabels):
                weighted_hist[j] = 0

            for j in range(len(tdata.contents[i])):
                possible = self.pixel_hist.contents[j][self.round_value(j, tdata.contents[i][j])]
                for outcome in possible.keys():
                    if outcome != "total":
                        weighted_hist[outcome] += self.alphas[j] * possible[outcome] / possible["total"]

            curr_max, argmax = -100, -100
            for j in range(self.nlabels):
                if weighted_hist[j] > curr_max or curr_max == -100:
                    curr_max = weighted_hist[j]
                    argmax = j
            if argmax != tlabels.contents[i]:
                errors += 1

        print("Testing complete: {0} mistakes were made".format(errors))