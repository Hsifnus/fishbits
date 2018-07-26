class DataCollection:

    def __init__(self, size, magic):
        self.max_size, self.size = size, 0
        self.magic, self.contents = magic, []

    def append(self, item):
        if self.size >= self.max_size:
            print("Warning: max size for collection ({0}) exceeded".format(self.max_size))
            return
        self.contents.append(item)
        self.size += 1

def parse(name, type):

    if type not in ["image", "label"]:
        print("Invalid file type for parser: " + type)
        return None

    try:
        contents = open(name, 'rb').read()
    except OSError:
        print("Failed to open file: " + name)
        return None

    numify = lambda s: int.from_bytes(s, byteorder="big")
    data = DataCollection(numify(contents[4:8]), numify(contents[0:4]))

    if type == "image":
        rows, cols, index = numify(contents[8:12]), numify(contents[12:16]), 16
        for i in range(data.max_size):
            image = []
            for j in range(rows * cols):
                image.append(contents[index])
                index += 1
            data.append(image)
    else:
        for i in range(data.max_size):
            data.append(contents[8+i])

    return data
