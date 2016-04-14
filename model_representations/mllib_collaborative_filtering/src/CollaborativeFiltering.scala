/**
 * Created by devinhe on 4/8/16.
 */

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{MatrixFactorizationModel, ALS, Rating}
import org.apache.spark.rdd.RDD

object CollaborativeFiltering {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("MLLib Collaborative Filtering Test").set("spark.executor.memory", "8g")
    val sc = new SparkContext(conf)
//    val data = sc.textFile("/Users/devinhe/research/262/server/data/ml-latest-small/ratings-nohead.csv")
    val data = sc.textFile("/Users/devinhe/research/262/lenskit/data/ratings_all.csv")
    val ratings = data.map(_.split(',') match { case Array(user, item, rate, timestamp) =>
      (Rating(user.toInt, item.toInt, rate.toDouble), timestamp.toLong)
    })

    val training = ratings.filter(x => x._2 % 10 < 8).map(x => x._1).cache()
    val validation = ratings.filter(x => x._2 % 10 >= 8).map(x => x._1).cache()
    val numTraining = training.count()
    val numValidation = validation.count()

    println("Training: " + numTraining + ", validation: " + numValidation)

//    val ranks = List(10, 20, 30, 50, 70, 100, 150, 200)
    val ranks = List(500, 800, 1000, 1500, 2000)
    var RMSEs: List[Double] = List()
//    val numIterations = 10
    val numIterations = 20
    for (rank <- ranks) {
      val model = ALS.train(training, rank, numIterations, 0.01)
      val validationRmse = computeRmse(model, validation, numValidation)
      RMSEs = RMSEs ++ List(validationRmse)
      val userFeatures = model.userFeatures
      val productFeatures = model.productFeatures
      userFeatures.coalesce(1).map(x => rowToString(x)).saveAsTextFile("collab-filtering-model/rank" + rank + "/user")

      productFeatures.coalesce(1).map(x => rowToString(x)).saveAsTextFile("collab-filtering-model/rank" + rank + "/product")
      model.save(sc, "collab-filtering-model/rank"+rank+"/model")
    }
    println(RMSEs)

  }

  def rowToString(row: (Int, Array[Double])): String = {
    var str = row._1.toString()
    row._2.foreach(x => str += "," + x.toString())
    str
  }

  def computeRmse(model: MatrixFactorizationModel, data: RDD[Rating], n: Long): Double = {
    val predictions: RDD[Rating] = model.predict(data.map(x => (x.user, x.product)))
    val predictionsAndRatings = predictions.map(x => ((x.user, x.product), x.rating))
      .join(data.map(x => ((x.user, x.product), x.rating)))
      .values
    math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_ + _) / n)
  }


}
