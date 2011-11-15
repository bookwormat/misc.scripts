#!/usr/bin/env ruby
# convert - a simple unit converter
# Copyright (C) 2008  Benjamin Ferrari
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
# This class does simple unit convertions 
# the constructor expects an io object that contains a simple rules of 
# the form sourceunit targetunit factor. All lines in a rules file that
# start with the character '#' are comments and will be ignored.
# 
# For example, a rules file could look like this:
# km m 1000
# in cm 2.54
# 
# this will automatically create 4 methods in the Converter:
# km_to_m, m_to_km, in_to_cm and cm_to_in
#
#Example: convert km m 10  
#output: 10000

class Converter

  #create a method that sends the message 'op' with the argument 
  #'value' to the first argument of the function
  def self.create_method from, to, op, factor
    name = "#{from}_to_#{to}".to_sym
    define_method(name) do |arg|
      arg.send(op,factor.to_f)
    end
  end


  def initialize(io)
    @io = io                 
    #read through all the rules and create 2 
    #methods for each rule (from unit1 to unit2 and vice versa)
    @io.readlines.each do |line| 
      #ignore lines that start with #  (comments in rules file)
      next if line =~ /^ +\#.*$/ 
      sunit, tunit, factor = line.split(/ +/)
      Converter.create_method sunit, tunit, "*", factor
      Converter.create_method tunit, sunit, "/", factor
    end

  end

end

if $0 == __FILE__
  from,to,factor = ARGV
  puts Converter.new(File.new("rules")).send("#{from}_to_#{to}",factor.to_f)
end
