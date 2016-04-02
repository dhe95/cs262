import numpy as np
from sklearn.ensemble import RandomForestClassifier

np.random.seed(0)

class Tree():
    TREE_LEAF = -1
    TREE_UNDEFINED = -2
    def __init__(self, tree):
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
        return max([i for i in range(2)], key=lambda x: self.value[node][0][x])

def forest_predict(val, trees):
    votes = [0,0]
    for tree in trees:
        votes[tree.predict(val)] += 1
    return max([i for i in range(2)], key=lambda x: votes[x])


# sample data
x = np.array([[0, 0], [0, 1], [0.5, 3], [0.25, 4], [2, 3.5], [3, 3.9], [4, 3], [1.5, 0], [1.7, 2.9], [2.0, 0.5], [2.1, 3.9], [3.3, 2.5], [3.8, 0.2], [1.1, 2.6]])
y = np.array([1.0 for i in range(7)] + [0.0 for i in range(7)])

model = RandomForestClassifier()
model.fit(x, y)

# the trees here and model estimators will be the same
trees = map(lambda x: Tree(x.tree_), model.estimators_)
model.estimators_[0].predict([10,0])
