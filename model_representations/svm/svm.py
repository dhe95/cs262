from sklearn import svm
import numpy as np


class SVM:

    def __init__(self, skSVM):
        self.support = skSVM.support_
        self.SV = skSVM.support_vectors_
        self.nSV = skSVM.n_support_
        self.sv_coef = skSVM._dual_coef_
        self.intercept = skSVM._intercept_
        self.probA = skSVM.probA_
        self.probB_ = skSVM.probB_
        self.kernel = skSVM.kernel
        self.degree = skSVM.degree
        self.coef0 = skSVM.coef0
        self.gamma = skSVM._gamma
        self.cache_size = skSVM.cache_size
        self.coef = skSVM.coef_
        self.classes = skSVM.classes_

    def predict(self, X):
        nr_class = self.classes.shape[0]
        l = self.SV.shape[0]
        kvalue = np.zeros(l, dtype=np.float64)
        for i in range(l):
            kvalue[i] = X.dot(self.SV[i]) # kernel function for other kernel types

        start = np.zeros(nr_class, dtype=np.int32)
        start[0] = 0
        for i in range(1, nr_class):
            start[i] = start[i-1] + self.nSV[i-1]

        vote = np.zeros(nr_class, dtype=np.int32)

        p = 0
        for i in range(nr_class):
            for j in range(i+1, nr_class):
                sum = 0.0
                si = start[i]
                sj = start[j]
                ci = self.nSV[i]
                cj = self.nSV[j]

                coef1 = self.sv_coef[j-1]
                coef2 = self.sv_coef[i]
                for k in range(ci):
                    sum += coef1[si+k] * kvalue[si+k]
                for k in range(cj):
                    sum += coef2[sj+k] * kvalue[sj+k]
                sum -= self.intercept[p]
                if sum > 0:
                    vote[i] += 1
                else:
                    vote[j] += 1
                p += 1
        vote_max_idx = max([i for i in range(nr_class)], key=lambda x: vote[x])
        return self.classes[vote_max_idx]


    # linear kernel
    def predict_linear(self, X):
        return self.coef[0].dot(X) - self.intercept


x = np.array([[0, 0], [0, 1], [0.5, 3], [0.25, 4], [2, 3.5], [3, 3.9], [4, 3], [1.5, 0], [1.7, 2.9], [2.0, 0.5], [2.1, 3.9], [3.3, 2.5], [3.8, 0.2], [1.1, 2.6]])
y = np.array([1.0 for i in range(7)] + [0.0 for i in range(7)])

clf = svm.SVC(kernel='linear')
clf.fit(x, y)

model = SVM(clf)

test = np.array([[0,0], [10,0], [-5,5]])
clf.predict(test[0])
for point in test:
    print model.predict(point), clf.predict(point)[0]

