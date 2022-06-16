data <- read.csv (file="FUN.tsv", head=FALSE, sep="	")

 library(lattice)

data <- as.matrix(data)
data[, 1] <- data[, 1] /100

wireframe(data, xlab="Tiempo", ylab="Drones", zlab="Operadores")