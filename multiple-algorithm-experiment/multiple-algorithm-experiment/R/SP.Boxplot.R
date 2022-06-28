postscript("SP.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data"
qIndicator <- function(indicator, problem)
{
fileNGSAII<-paste(resultDirectory, "NGSAII", sep="/")
fileNGSAII<-paste(fileNGSAII, problem, sep="/")
fileNGSAII<-paste(fileNGSAII, indicator, sep="/")
NGSAII<-scan(fileNGSAII)

filePESA2<-paste(resultDirectory, "PESA2", sep="/")
filePESA2<-paste(filePESA2, problem, sep="/")
filePESA2<-paste(filePESA2, indicator, sep="/")
PESA2<-scan(filePESA2)

algs<-c("NGSAII","PESA2")
boxplot(NGSAII,PESA2,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(2,3))
indicator<-"SP"
qIndicator(indicator, "square1")
qIndicator(indicator, "square2")
qIndicator(indicator, "square3")
qIndicator(indicator, "square4")
