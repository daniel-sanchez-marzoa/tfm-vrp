# Desde gnuplot: load "valdebueyes.plot"
# Barridos de: valdebueyes.csv
barridos="valdebueyes.csv"
# Estación de control en: valdebueyes-CS.csv
estacion="valdebueyes-CS.csv"

#dibujando (NOTA: se gira 90grados)
set grid
set key left
plot barridos using 2:1:($4-$2):($3-$1) t "Valdebueyes" with vectors heads, estacion u 2:1 t "CS" w p
