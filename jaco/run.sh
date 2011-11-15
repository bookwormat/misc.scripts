if [ ! -e target/ACO-jar-with-dependencies.jar ] 
then
	echo "jar file does not exist. build first."
	exit 1
fi

if [ ! -d logs ]
then
	mkdir logs
fi

for f in ../../mebp/*.dat
do
	filename=`echo $f | cut -d / -f 4`
	for ((  i = 0 ;  i <= 10;  i++  ))
	do
  		echo "ACO run #$i for $filename"
		java -jar target/ACO-jar-with-dependencies.jar $f > "logs/$filename-$i.log"
	done
done