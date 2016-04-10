/**
 * Created by devinhe on 4/8/16.
 */

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating

object CollaborativeFiltering {

  def main(args: Array[String]): Unit = {
    var conf = new SparkConf().setAppName("MLLib Collaborative Filtering Test")
    val sc = new SparkContext(conf)
//    val data = sc.textFile("/Users/devinhe/research/262/server/data/ml-latest-small/ratings-nohead.csv")
    val data = sc.textFile("/Users/devinhe/research/262/lenskit/data/ratings_all.csv")
    val ratings = data.map(_.split(',') match { case Array(user, item, rate, timestamp) =>
      Rating(user.toInt, item.toInt, rate.toDouble)
    })

    val rank = 75
    val numIterations = 10
    val model = ALS.train(ratings, rank, numIterations, 0.01)

    val userFeatures = model.userFeatures
    val productFeatures = model.productFeatures


    val usersProducts = ratings.map { case Rating(user, product, rate) =>
      (user, product)
    }
    val predictions =
      model.predict(usersProducts).map { case Rating(user, product, rate) =>
        ((user, product), rate)
      }
    val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
      ((user, product), rate)
    }.join(predictions)
    val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
      val err = (r1 - r2)
      err * err
    }.mean()
    println("Mean Squared Error = " + MSE)

//    userFeatures.coalesce(1).map(x => rowToString(x)).saveAsTextFile("collab-filtering-model/rank75/user")
//
//    productFeatures.coalesce(1).map(x => rowToString(x)).saveAsTextFile("collab-filtering-model/rank75/product")
//    model.save(sc, "collab-filtering-model/rank75/model")
  }

  def rowToString(row: (Int, Array[Double])): String = {
    var str = row._1.toString()
    row._2.foreach(x => str += "," + x.toString())
    str
  }


}
