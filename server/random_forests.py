import pickle
import numpy as np
from sklearn.ensemble import RandomForestClassifier

np.random.seed(0)

class RandomForest:

    def __init__(self, skmodel):
        self.classes = skmodel.classes_
        self.trees = map(lambda x: Tree(x.tree_, len(self.classes)), skmodel.estimators_)

    def predict(self, val):
        votes = np.zeros(len(self.classes))
        for tree in self.trees:
            votes[tree.predict(val)] += 1
        return max([i for i in range(len(self.classes))], key=lambda x: votes[x])

class Tree():
    TREE_LEAF = -1
    TREE_UNDEFINED = -2

    def __init__(self, tree, n_classes):
        self.n_classes = n_classes
        self.left = tree.children_left
        self.right = tree.children_right
        self.feature = tree.feature
        self.threshold = tree.threshold
        self.value = tree.value

    def predict(self, x):
        node = 0
        while self.left[node] != Tree.TREE_LEAF and self.right[node] != Tree.TREE_LEAF:
            feature_to_split = self.feature[node]
            if x[feature_to_split] <= self.threshold[node]:
                node = self.left[node]
            else:
                node = self.right[node]
        return max([i for i in range(self.n_classes)], key=lambda x: self.value[node][0][x])

if __name__ == "__main__":
    training = "data/digit/train-images.csv"
    training_labels = "data/digit/train-labels.csv"
    test = "data/digit/test-images.csv"
    test_labels = "data/digit/test-labels.csv"
    train_x = np.loadtxt(open(training,"rb"),delimiter=",")
    train_y = np.loadtxt(open(training_labels, "rb"), delimiter=",")
    test_x = np.loadtxt(open(test,"rb"),delimiter=",")
    test_y = np.loadtxt(open(test_labels, "rb"), delimiter=",")

    num_trees = [10, 50, 100, 200, 300, 500, 800, 1000]
    scores = []
    for n in num_trees:
        model = RandomForestClassifier(n_estimators=n).fit(train_x, train_y)
        scores.append(model.score(test_x, test_y))
        s = pickle.dumps(model)
        f = open("digits_forest_" + str(n) + "trees.pkl", "w")
        f.write(s)

    print scores
