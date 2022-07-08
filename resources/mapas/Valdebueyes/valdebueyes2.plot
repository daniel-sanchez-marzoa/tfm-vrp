# Desde gnuplot: load "valdebueyes.plot"
# Barridos de: valdebueyes2.csv
barridos="valdebueyes2.csv"
# Estación de control en: valdebueyes2-CS.csv
estacion="valdebueyes2-CS.csv"

#dibujando (NOTA: se gira 90grados)
set grid
set key bottom left
plot barridos using 2:1:($4-$2):($3-$1) t "Valdebueyes" with vectors heads, estacion u 2:1 t "CS" w p
