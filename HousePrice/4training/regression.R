
train=read.csv("~/Documents/classes/B565GroupProject/HousePrice/data/train_reduced.csv")
test=read.csv("~/Documents/classes/B565GroupProject/HousePrice/data/test_reduced.csv")
 
X=train[,2:105]
y=train[,106] 

model <- lm(y~X[,1]+X[,2]+X[,3]+X[,4]+X[,5]+X[,6]+X[,7]+X[,8]+X[,9]+X[,10]+X[,11]+X[,12]+X[,13]+X[,14]+X[,15]+X[,16]+X[,17]+X[,18]+X[,19]+X[,20]+X[,21]+X[,22]+X[,23]+X[,24]+X[,25]+X[,26]+X[,27]+X[,28]+X[,29]+X[,30]+X[,31]+X[,32]+X[,33]+X[,34]+X[,35]+X[,36]+X[,37]+X[,38]+X[,39]+X[,40], data=train)


TEST_SIZE=dim(test)[1]
res =matrix(rep(0,TEST_SIZE),nrow=TEST_SIZE,ncol=1)
for(j in seq(1, TEST_SIZE,1)){
	t=model$coefficients[[1]]
	for(i in seq(2,41,1)){
		t= t+ model$coefficients[[i]] * test[j,i]
	}
	res[j]=t
}

res=cbind(test$Id, res)
colnames(res)=c("Id","SalePrice")
write.table(res, "~/Documents/classes/B565GroupProject/HousePrice/data/test_results.csv",sep=",",row.names=F)