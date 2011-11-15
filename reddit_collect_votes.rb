# file: reddit_collect_votes.rb
# (C) 2008 Benjamin Ferrari 
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
# Usecase scenario: You want to keep track of the developing of up-
# and downvotes of a new reddit submission over time.
#
# The script takes an URL of the details page of a reddit post and prints out the
# points, upvotes and downvotes (in that order) as a comma seperated
# list.
# 
# The script is meant to be together with a scheduler (like unix crontab). 
#
# example: 
# ruby reddit_collect_votes.rb http://programming.reddit.com/info/2gwce/details
# output: 30,44,14
#
# run this on a crontab, lets say, every full hour, and append the
# output to a file. Then, after a while, import the output with into a
# spreadsheet, a statistics program or another script to further
# process the content.
#
# the crontab entry could look like this:
#
# 30,0 * * * * ruby /home/ben/bin/reddit_collect_votes.rb http://programming.reddit.com/info/1bf55/details >> /home/ben/log/emacs_article.csv
#
require "open-uri"
require "rexml/document"
doc = REXML::Document.new(open(ARGV.first).read)
query = "//table[@class='details']//td[not(@class)]"
stats = doc.elements.to_a(query)[1..-1].collect{|x| x.text.to_i} + [Time.now.iso8601]
puts stats.join(",")
