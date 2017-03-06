library(randomForest)
set.seed(1341)

-------------------------- Working with Reduced LOG Train Data ------------------------------------------------

train_reduced <- read.csv("train_reduced.csv")
train_data <- train_reduced[2:106]

RC.rf <- randomForest(log(SalePrice) ~ ., train_data, importance = TRUE, proximity = TRUE, do.trace = 100)
#     |      Out-of-bag   |
#Tree |      MSE  %Var(y) |
# 100 |  0.02679    16.80 |
# 200 |  0.02653    16.64 |
# 300 |  0.02628    16.48 |
# 400 |  0.02623    16.45 |
# 500 |  0.02625    16.46 |

Prediction <- exp(predict(RC.rf, train_reduced))
submit <- data.frame(HouseID = train_reduced$Id, HousePrice = Prediction)
write.csv(submit, file = "House_RanFor_Train.csv", row.names = FALSE)

mean((RC.rf$predicted-train_reduced$SalePrice[as.numeric(names(RC.rf$predicted))])^2)
#39034872525
mean(RC.rf$predicted-train_reduced$SalePrice)
#-180909.2

---------------------------------------- For Test Data Prediction ----------------------------------------

test_reduced <- read.csv("test_reduced.csv")
test_data <- test_reduced[2:105]
Prediction <- exp(predict(RC.rf, test_data))
submit <- data.frame(HouseID = test_reduced$Id, HousePrice = Prediction)
write.csv(submit, file = "House_RanFor_test.csv", row.names = FALSE)