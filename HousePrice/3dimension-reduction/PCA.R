train=read.csv("/Users/Ethan/Documents/classes/B565GroupProject/HousePrice/data/train_dummies.csv")

test=read.csv("/Users/Ethan/Documents/classes/B565GroupProject/HousePrice/data/test_dummies.csv")

TRAIN_SIZE=dim(train)[1]
TEST_SIZE=dim(test)[1]

numOfCol=dim(train)[2]

salePrice=train$SalePrice
 
data=rbind(train[,c(-numOfCol)],test)

data.pca <- princomp(data[2:(numOfCol-1)])
summary(data.pca)

#CHOOSE TOP 104 COMPONENTS
TOP=104;


reduced_data=data.pca$scores[,c(1:TOP)]
colName=colnames(reduced_data)
colName=c("Id", colName);
train_reduced=cbind(train$Id, reduced_data[1: TRAIN_SIZE,])
train_reduced=cbind(train_reduced, salePrice)
train_colName=c( colName,"SalePrice")
colnames(train_reduced)= train_colName

test_reduced =cbind(test$Id, reduced_data[(TRAIN_SIZE+1):(TRAIN_SIZE+ TEST_SIZE),]) 
colnames(test_reduced)= colName

write.table(train_reduced,"~/Documents/classes/B565GroupProject/HousePrice/data/train_reduced.csv", row.names=FALSE, sep=",")
write.table(test_reduced,"~/Documents/classes/B565GroupProject/HousePrice/data/test_reduced.csv", row.names=FALSE, sep=",")




