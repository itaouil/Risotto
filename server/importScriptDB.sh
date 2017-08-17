#!/bin/bash
# Purpose: Import data into database's tables.
# Author: Ilyass Taouil.
# Last updated on : 04-May-2016
# -----------------------------------------------

# File declaration
FILE=restaurantdatabase.db

# Execute
if [ -e "$FILE" ]
then
	# Import data
	sqlite3 restaurantdatabase.db ".import tables.txt RestaurantTable"
	sqlite3 restaurantdatabase.db ".import meals.txt Meal"
else
	echo "$FILE not found!"
fi
