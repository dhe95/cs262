import numpy as np
from sklearn.ensemble import RandomForestClassifier

np.random.seed(0)

class RandomForest:
    
    def __init__(self, skmodel):
        self.classes = skmodel.classes_
        self.trees = map(lambda x: Tree(x.tree_, len(self.classes)), model.estimators_)

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



# sample data
x = np.array([[0, 0], [0, 1], [0.5, 3], [0.25, 4], [2, 3.5], [3, 3.9], [4, 3], [1.5, 0], [1.7, 2.9], [2.0, 0.5], [2.1, 3.9], [3.3, 2.5], [3.8, 0.2], [1.1, 2.6]])
y = np.array([1.0 for i in range(5)] + [0.0 for i in range(5)] + [2.0 for i in range(4)])

model = RandomForestClassifier()
model.fit(x, y)

# the trees here and model estimators will be the same
pymodel = RandomForest(model)
for x in range(-5, 5):
    for y in range(-5,5):
        point = [x,y]
        print pymodel.predict(point), model.predict(point)
