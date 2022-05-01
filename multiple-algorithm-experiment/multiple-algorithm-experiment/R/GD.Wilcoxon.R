write("", "GD.Wilcoxon.tex",append=FALSE)
resultDirectory<-"../data"
latexHeader <- function() {
  write("\\documentclass{article}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\title{StandardStudy}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\usepackage{amssymb}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\author{A.J.Nebro}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\begin{document}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\maketitle", "GD.Wilcoxon.tex", append=TRUE)
  write("\\section{Tables}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\", "GD.Wilcoxon.tex", append=TRUE)
}

latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\caption{", "GD.Wilcoxon.tex", append=TRUE)
  write(problem, "GD.Wilcoxon.tex", append=TRUE)
  write(".GD.}", "GD.Wilcoxon.tex", append=TRUE)

  write("\\label{Table:", "GD.Wilcoxon.tex", append=TRUE)
  write(problem, "GD.Wilcoxon.tex", append=TRUE)
  write(".GD.}", "GD.Wilcoxon.tex", append=TRUE)

  write("\\centering", "GD.Wilcoxon.tex", append=TRUE)
  write("\\setlength\\tabcolsep{1pt}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\begin{scriptsize}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\begin{tabular}{", "GD.Wilcoxon.tex", append=TRUE)
  write(tabularString, "GD.Wilcoxon.tex", append=TRUE)
  write("}", "GD.Wilcoxon.tex", append=TRUE)
  write(latexTableFirstLine, "GD.Wilcoxon.tex", append=TRUE)
  write("\\hline ", "GD.Wilcoxon.tex", append=TRUE)
}

printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { 
  file1<-paste(resultDirectory, algorithm1, sep="/")
  file1<-paste(file1, problem, sep="/")
  file1<-paste(file1, indicator, sep="/")
  data1<-scan(file1)
  file2<-paste(resultDirectory, algorithm2, sep="/")
  file2<-paste(file2, problem, sep="/")
  file2<-paste(file2, indicator, sep="/")
  data2<-scan(file2)
  if (i == j) {
    write("-- ", "GD.Wilcoxon.tex", append=TRUE)
  }
  else if (i < j) {
    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) <= median(data2)) {
        write("$\\blacktriangle$", "GD.Wilcoxon.tex", append=TRUE)
}
      else {
        write("$\\triangledown$", "GD.Wilcoxon.tex", append=TRUE)
}
    }
    else {
      write("--", "GD.Wilcoxon.tex", append=TRUE)
    }
  }
  else {
    write(" ", "GD.Wilcoxon.tex", append=TRUE)
  }
}

latexTableTail <- function() { 
  write("\\hline", "GD.Wilcoxon.tex", append=TRUE)
  write("\\end{tabular}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\end{scriptsize}", "GD.Wilcoxon.tex", append=TRUE)
  write("\\end{table}", "GD.Wilcoxon.tex", append=TRUE)
}

latexTail <- function() { 
  write("\\end{document}", "GD.Wilcoxon.tex", append=TRUE)
}

### START OF SCRIPT 
# Constants
problemList <-c("a280.vrp", "ali535.vrp", "att48.vrp", "att532.vrp", "eil101.vrp", "euclidA300.vrp", "euclidB300.vrp", "kroA100 copy.vrp", "kroA100.vrp", "kroB100.vrp", "pr124.vrp") 
algorithmList <-c("ESPEA", "NGSAII", "MOCell", "PESA2", "SMSEMOA", "SPEA2", "GWASFGA") 
tabularString <-c("lcccccc") 
latexTableFirstLine <-c("\\hline  & NGSAII & MOCell & PESA2 & SMSEMOA & SPEA2 & GWASFGA\\\\ ") 
indicator<-"GD"

 # Step 1.  Writes the latex header
latexHeader()
tabularString <-c("| l | ccccccccccc | ccccccccccc | ccccccccccc | ccccccccccc | ccccccccccc | ccccccccccc | ") 

latexTableFirstLine <-c("\\hline \\multicolumn{1}{|c|}{} & \\multicolumn{11}{c|}{NGSAII} & \\multicolumn{11}{c|}{MOCell} & \\multicolumn{11}{c|}{PESA2} & \\multicolumn{11}{c|}{SMSEMOA} & \\multicolumn{11}{c|}{SPEA2} & \\multicolumn{11}{c|}{GWASFGA} \\\\") 

# Step 3. Problem loop 
latexTableHeader("a280.vrp ali535.vrp att48.vrp att532.vrp eil101.vrp euclidA300.vrp euclidB300.vrp kroA100 copy.vrp kroA100.vrp kroB100.vrp pr124.vrp ", tabularString, latexTableFirstLine)

indx = 0
for (i in algorithmList) {
  if (i != "GWASFGA") {
    write(i , "GD.Wilcoxon.tex", append=TRUE)
    write(" & ", "GD.Wilcoxon.tex", append=TRUE)

    jndx = 0
    for (j in algorithmList) {
      for (problem in problemList) {
        if (jndx != 0) {
          if (i != j) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "GD.Wilcoxon.tex", append=TRUE)
          } 
          if (problem == "pr124.vrp") {
            if (j == "GWASFGA") {
              write(" \\\\ ", "GD.Wilcoxon.tex", append=TRUE)
            } 
            else {
              write(" & ", "GD.Wilcoxon.tex", append=TRUE)
            }
          }
     else {
    write("&", "GD.Wilcoxon.tex", append=TRUE)
     }
        }
      }
      jndx = jndx + 1
}
    indx = indx + 1
  }
} # for algorithm

  latexTableTail()

#Step 3. Writes the end of latex file 
latexTail()

