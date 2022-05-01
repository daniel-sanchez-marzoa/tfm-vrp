postscript("GD.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data"
qIndicator <- function(indicator, problem)
{
fileESPEA<-paste(resultDirectory, "ESPEA", sep="/")
fileESPEA<-paste(fileESPEA, problem, sep="/")
fileESPEA<-paste(fileESPEA, indicator, sep="/")
ESPEA<-scan(fileESPEA)

fileNGSAII<-paste(resultDirectory, "NGSAII", sep="/")
fileNGSAII<-paste(fileNGSAII, problem, sep="/")
fileNGSAII<-paste(fileNGSAII, indicator, sep="/")
NGSAII<-scan(fileNGSAII)

fileMOCell<-paste(resultDirectory, "MOCell", sep="/")
fileMOCell<-paste(fileMOCell, problem, sep="/")
fileMOCell<-paste(fileMOCell, indicator, sep="/")
MOCell<-scan(fileMOCell)

filePESA2<-paste(resultDirectory, "PESA2", sep="/")
filePESA2<-paste(filePESA2, problem, sep="/")
filePESA2<-paste(filePESA2, indicator, sep="/")
PESA2<-scan(filePESA2)

fileSMSEMOA<-paste(resultDirectory, "SMSEMOA", sep="/")
fileSMSEMOA<-paste(fileSMSEMOA, problem, sep="/")
fileSMSEMOA<-paste(fileSMSEMOA, indicator, sep="/")
SMSEMOA<-scan(fileSMSEMOA)

fileSPEA2<-paste(resultDirectory, "SPEA2", sep="/")
fileSPEA2<-paste(fileSPEA2, problem, sep="/")
fileSPEA2<-paste(fileSPEA2, indicator, sep="/")
SPEA2<-scan(fileSPEA2)

fileGWASFGA<-paste(resultDirectory, "GWASFGA", sep="/")
fileGWASFGA<-paste(fileGWASFGA, problem, sep="/")
fileGWASFGA<-paste(fileGWASFGA, indicator, sep="/")
GWASFGA<-scan(fileGWASFGA)

algs<-c("ESPEA","NGSAII","MOCell","PESA2","SMSEMOA","SPEA2","GWASFGA")
boxplot(ESPEA,NGSAII,MOCell,PESA2,SMSEMOA,SPEA2,GWASFGA,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(2,3))
indicator<-"GD"
qIndicator(indicator, "a280.vrp")
qIndicator(indicator, "ali535.vrp")
qIndicator(indicator, "att48.vrp")
qIndicator(indicator, "att532.vrp")
qIndicator(indicator, "eil101.vrp")
qIndicator(indicator, "euclidA300.vrp")
qIndicator(indicator, "euclidB300.vrp")
qIndicator(indicator, "kroA100 copy.vrp")
qIndicator(indicator, "kroA100.vrp")
qIndicator(indicator, "kroB100.vrp")
qIndicator(indicator, "pr124.vrp")
