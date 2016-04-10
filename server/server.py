from flask import Flask, request, jsonify
import pickle

SVM_TRAINING_FILE = "data/digit-dataset/data-training.csv"
RANDOM_FOREST_TRAINING_FILE = "data/digit-dataset/data-training.csv"
SVM_MODEL_FILE = "digits_svm.pkl"
RANDOM_FOREST_MODEL_FILE = "digits_forest.pkl"

app = Flask(__name__)

def load_model_from_file(file_path):
    f = open(file_path, "r")
    pymodel = pickle.loads(f.read())
    f.close()
    return pymodel

@app.route("/svm_predict", methods=['POST'])
def svm_predict():
    content = request.get_json()
    result = { "result": svm.predict(content['x'])[0] }
    return jsonify(result)

@app.route("/random_forest_predict", methods=['POST'])
def random_forest_predict():
    content = request.get_json()
    result = { "result": random_forest.predict(content['x'])[0] }
    return jsonify(result)

if __name__ == "__main__":
    svm = load_model_from_file(SVM_MODEL_FILE)
    random_forest = load_model_from_file(RANDOM_FOREST_MODEL_FILE)
    app.debug = True
    app.run()
