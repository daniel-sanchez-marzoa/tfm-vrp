postscript("GD.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data"
qIndicator <- function(indicator, problem)
{
filengsaii2<-paste(resultDirectory, "ngsaii2", sep="/")
filengsaii2<-paste(filengsaii2, problem, sep="/")
filengsaii2<-paste(filengsaii2, indicator, sep="/")
ngsaii2<-scan(filengsaii2)

filengsaii1<-paste(resultDirectory, "ngsaii1", sep="/")
filengsaii1<-paste(filengsaii1, problem, sep="/")
filengsaii1<-paste(filengsaii1, indicator, sep="/")
ngsaii1<-scan(filengsaii1)

algs<-c("ngsaii2","ngsaii1")
boxplot(ngsaii2,ngsaii1,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(2,3))
indicator<-"GD"
qIndicator(indicator, "VRP")
