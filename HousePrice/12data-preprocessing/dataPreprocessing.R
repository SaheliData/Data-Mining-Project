
#healper functions
#if NA is a type of the feature, assign NTYPE to it to avoid confusion
assign_NTYPE <-function(x){
	x[is.na(x)] <- "NONETYPE"
	x
}

#fill missing values with sample median
fill_median <- function(x) {
	sp_mean <- mean(x[!is.na(x)])
	x[is.na(x)] <- sp_mean
	x
}
#fill missing values with sample mode
fill_mode <- function(x) {
	sp_mode <- getMode(x[!is.na(x)])
	x[is.na(x)] <- sp_mode
	x
}
#normalization function, scale to 0~ DEFAULT_SCALE.
normalize <- function(x){
	maxVal <- max(x)
	x / maxVal * DEFAULT_SCALE;
}
#get mode of the feature
getMode <- function(v){
	uniqv <- unique(v)
	uniqv[which.max(tabulate(match(v, uniqv)))]
}







library(dummies)

#Read data
train = read.csv("~/Documents/classes/B565GroupProject/HousePrice/data/train.csv");

test = read.csv("~/Documents/classes/B565GroupProject/HousePrice/data/test.csv");

TRAIN_SIZE=dim(train)[1]
TEST_SIZE=dim(test)[1]

#data type confirmation
#Impute missing values
  #for categorial data, simply use mode;
  #for numerial data, simply use median.robust to outliers than using mean
#It's hard to distinguish outliers in this data. so skip this;
#Normalization (for numerial data)
DEFAULT_SCALE = 10

#all numeric features;
numericFeatures=c(4,5,18,19,20,21,27,35,37,38,39,44,45,46,47,48,49,50,51,52,53,55,57,60,62,63,67,68,69,70,71,72,76,77,78);

#for some features, NA is another type. 
features_na_as_type=c(7,31,32,33,34,36,58,59,61,64,65,73,74,75);

for (i in seq(2,80,1)){
	#if it's numeric feature
	if(is.element(i, numericFeatures)){
		#type confirmation
		train[[i]] = as.numeric(train[[i]])
		test[[i]] = as.numeric(test[[i]])
		
		#impute missing values
		train[[i]]=fill_median(train[[i]])
		test[[i]] = fill_median(test[[i]])
		
		
		#normalization;  
		train[[i]]= normalize(train[[i]])
		test[[i]] = normalize(test[[i]])
		 
		
	}else{#if it's categorial feature
		#type confirmation
		
		#later on we will do dummy vector; we need training data and test data to have same levels on every categorial feature
		train_and_test=rbind(train[i],test[i]);
		train_and_test[[1]]=as.factor(train_and_test[[1]])
		
		if(is.element(i, features_na_as_type)){
			levels(train_and_test[[1]]) <- c(levels(train_and_test[[1]]), "NONETYPE")
		}
		train[[i]]=train_and_test[[1]][1:TRAIN_SIZE]
		test[[i]]=train_and_test[[1]][ (TRAIN_SIZE +1): (TRAIN_SIZE +TEST_SIZE)]
		
		#impute missing values
		#impute missing values;if NA is not a type of the feature
		#if NA is a type of the feature, assign NONETYPE to it to avoid confusion
		if(!is.element(i, features_na_as_type)){
			train[[i]]=fill_mode(train[[i]])
			test[[i]] = fill_mode(test[[i]])
		}else{
			train[[i]]= assign_NTYPE(train[[i]])
			test[[i]] = assign_NTYPE(test[[i]])
		}
		
	}
}


#Write xxx_clean.csv
write.table(train, "~/Documents/classes/B565GroupProject/HousePrice/data/train_clean.csv", row.names=FALSE,sep=",")
write.table(test, "~/Documents/classes/B565GroupProject/HousePrice/data/test_clean.csv",row.names=FALSE,sep=",")


#produce dummy vectors; numerial data untouched.


train_dummies = dummy.data.frame(train,drop=FALSE)
test_dummies = dummy.data.frame(test,drop=FALSE)

write.table(train_dummies, "~/Documents/classes/B565GroupProject/HousePrice/data/train_dummies.csv", row.names=FALSE,sep=",")
write.table(test_dummies, "~/Documents/classes/B565GroupProject/HousePrice/data/test_dummies.csv",row.names=FALSE,sep=",")






