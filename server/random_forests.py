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


def generate_model(training):
    d = np.loadtxt(open(training ,"rb"),delimiter=",")
    x = np.delete(d, 0, 1)
    y = d[:,0]

    model = RandomForestClassifier()
    model.fit(x, y)
    return model

if __name__ == "__main__":
    d = np.loadtxt(open("data/spam-dataset/data-test.csv", "rb"),delimiter=",")
    test_x = np.delete(d, 0, 1)
    test_y = d[:,0]
    pymodel = get_spam_random_forest()
    s = pickle.dumps(pymodel)
    f = open("digits_forest.pkl", "w")
    f.write(s)
    model = RandomForest(pymodel)
    pyaccuracy = 0.0
    modelaccuracy = 0.0
    for i, x in enumerate(test_x):
        if pymodel.predict(x) == test_y[i]:
            pyaccuracy += 1.0 / len(test_y)
        if model.predict(x) == test_y[i]:
            modelaccuracy += 1.0 / len(test_y)
    print pyaccuracy, modelaccuracy
