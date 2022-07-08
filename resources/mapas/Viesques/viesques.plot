# Desde gnuplot: load "viesques.plot"

# Barridos de: viesques-Swepts.csv
barridos="viesques-Swepts.csv"
# Estación de control en: viesques-CS.csv
estacion="viesques-CS.csv"

#dibujando (NOTA: se gira 90grados)
set grid
plot barridos using 2:1:($4-$2):($3-$1) t "Viesques" with vectors heads, estacion u 2:1 t "CS" w p
