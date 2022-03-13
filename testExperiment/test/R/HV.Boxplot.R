postscript("HV.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
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

filengsaii3<-paste(resultDirectory, "ngsaii3", sep="/")
filengsaii3<-paste(filengsaii3, problem, sep="/")
filengsaii3<-paste(filengsaii3, indicator, sep="/")
ngsaii3<-scan(filengsaii3)

filengsaii4<-paste(resultDirectory, "ngsaii4", sep="/")
filengsaii4<-paste(filengsaii4, problem, sep="/")
filengsaii4<-paste(filengsaii4, indicator, sep="/")
ngsaii4<-scan(filengsaii4)

filengsaii5<-paste(resultDirectory, "ngsaii5", sep="/")
filengsaii5<-paste(filengsaii5, problem, sep="/")
filengsaii5<-paste(filengsaii5, indicator, sep="/")
ngsaii5<-scan(filengsaii5)

filengsaii6<-paste(resultDirectory, "ngsaii6", sep="/")
filengsaii6<-paste(filengsaii6, problem, sep="/")
filengsaii6<-paste(filengsaii6, indicator, sep="/")
ngsaii6<-scan(filengsaii6)

filengsaii7<-paste(resultDirectory, "ngsaii7", sep="/")
filengsaii7<-paste(filengsaii7, problem, sep="/")
filengsaii7<-paste(filengsaii7, indicator, sep="/")
ngsaii7<-scan(filengsaii7)

filengsaii8<-paste(resultDirectory, "ngsaii8", sep="/")
filengsaii8<-paste(filengsaii8, problem, sep="/")
filengsaii8<-paste(filengsaii8, indicator, sep="/")
ngsaii8<-scan(filengsaii8)

filengsaii9<-paste(resultDirectory, "ngsaii9", sep="/")
filengsaii9<-paste(filengsaii9, problem, sep="/")
filengsaii9<-paste(filengsaii9, indicator, sep="/")
ngsaii9<-scan(filengsaii9)

filengsaii10<-paste(resultDirectory, "ngsaii10", sep="/")
filengsaii10<-paste(filengsaii10, problem, sep="/")
filengsaii10<-paste(filengsaii10, indicator, sep="/")
ngsaii10<-scan(filengsaii10)

filengsaii11<-paste(resultDirectory, "ngsaii11", sep="/")
filengsaii11<-paste(filengsaii11, problem, sep="/")
filengsaii11<-paste(filengsaii11, indicator, sep="/")
ngsaii11<-scan(filengsaii11)

algs<-c("ngsaii2","ngsaii1","ngsaii3","ngsaii4","ngsaii5","ngsaii6","ngsaii7","ngsaii8","ngsaii9","ngsaii10","ngsaii11")
boxplot(ngsaii2,ngsaii1,ngsaii3,ngsaii4,ngsaii5,ngsaii6,ngsaii7,ngsaii8,ngsaii9,ngsaii10,ngsaii11,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(2,3))
indicator<-"HV"
qIndicator(indicator, "kroA100.vrp")
qIndicator(indicator, "a280.vrp")
qIndicator(indicator, "ali535.vrp")
qIndicator(indicator, "att48.vrp")
qIndicator(indicator, "att532.vrp")
qIndicator(indicator, "eil101.vrp")
qIndicator(indicator, "euclidA300.vrp")
qIndicator(indicator, "euclidB300.vrp")
qIndicator(indicator, "kroA100 copy.vrp")
qIndicator(indicator, "kroB100.vrp")
qIndicator(indicator, "pr124.vrp")
