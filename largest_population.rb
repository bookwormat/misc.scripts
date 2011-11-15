#!/usr/bin/env ruby
#(c) 2007 Benjamin Ferrari
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#
######################### Documentation #################################
#
# largest_population.rb 
#
# find the largest population of mostly ones in a sequence of ones and zeros.
# returns the triple: startindex, endindex and the population itself.
#
# EXAMPLE:
#
# ruby largest_population.rb 2 00011001001110011000011111101101110 
# output: 22 34 1111110110111
#
#

# evaluate command line options
if ARGV.size < 1 or ARGV.size > 3 or ARGV.last.match(/[^01]/) or
    !(ARGV.last =~ /0/ and ARGV.last =~ /1/) then
  puts "USAGE: ruby largest_population.rb [ALLOWED_ZEROS] VECTOR"
  puts "EXAMPLE: ruby largest_population.rb 2 00011001001110011000011111101101110"
  puts "EXAMPLE: ruby largest_population.rb 00011001001110011000011111101101110"
  exit
end

vector = ARGV.last

if ARGV.size == 2 then
  max_num_zeros = ARGV.first.to_i
end


#If no max_num_zeros value is specified, we choose one using the function:
#
# max_num_zeros = lenseq(0) / lenseq(1) 
#
#where lenseq(X) is the length of the largest sequence of X found.
lenseq = lambda{|x| vector.scan(/#{x}+/).max.size + 1 }
max_num_zeros ||= lenseq.call(0) / lenseq.call(1)

#now we search for all substrings that contain less than  
#max_num_zeros zeros in a row. 
if max_num_zeros < 1 then
  #if no zeros are allowed at all, we can simply search for 
  #sequences of 1.
  populations = vector.scan(/1+/)
else 
  #split at points with too many zeros and remove leading and trailing zeros
  populations = vector.split(/0{#{max_num_zeros},}/).collect{|str|str.gsub(/(^0+)|(0+$)/,"")}
end

#find the largest population. 
largest_population = populations.max{|p1,p2|p1.length <=> p2.length}

#we search for the population in the vector to find the indices
start = vector.index largest_population 
stop = start + largest_population.size
  
puts "#{start+1} #{stop} #{largest_population}"
