# name: twitter lucene
# (C) 2007 Benjamin Ferrari 
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
#requires JRuby and Apache Lucene > 2.1.
#
#On my system, the complete call looks like this:  
#env CLASSPATH="lib/lucene-core-2.1.0.jar" /usr/local/bin/jruby twitter2lucene.rb myIndex 

#please note that the script will NOT index ALL posts from twitter!
#The API method "public_timeline.format" does not fetch all the posts
#since the last id, but only the last 20 or so. 
#
# I also did not invest much time to make this stable or accurate.   

require "open-uri"
require "rexml/document"
require "java"
require "logger"

include_class org.apache.lucene.analysis.standard.StandardAnalyzer
include_class org.apache.lucene.index.IndexWriter
include_class org.apache.lucene.index.Term
include_class org.apache.lucene.document.Document
include_class org.apache.lucene.document.Field

#fetch most recent posts from twitter and put them into a lucene index.
class TwitterIndexer

  def initialize index_directory
    @last_id = nil
    @log = Logger.new(STDOUT)
    @log.level = Logger::INFO
    @index_dir = index_directory
  end
  
  def index
    add_to_index next_public_timeline
  end

  private

  #fetch the next 20 posts (twitter people call them 'statuses') from
  #twitter.com, using the api method 'public_timeline.xml' .
  def next_public_timeline
    #If this is not the first call of this method, the 'since_id'
    #parameter will be used to fetch entries from that position on.
    param_str = @last_id ? "?since_id=#{@last_id}" : ""
    doc = REXML::Document.new open("http://twitter.com/statuses/public_timeline.xml#{param_str}").read
    statuses = []
    doc.elements.each("/statuses/status") do |status| 
      user = status.elements['user']
      location = user.elements['location'].text || "unknown"
      time = Time.parse(status.elements['created_at'].text).strftime("%Y%m%d%H%M%S")
      statuses << {:id => status.elements['id'].text, 
                  :text => status.elements['text'].text, 
                  :username =>  user.elements['name'].text,
                  :userid => user.elements['id'].text,
                  :time => time,
                  :location => location}
    end
    statuses
  end

  #All fields are stored in the index full text. Twitter does not
  #allow large text corpi anyway, and we do not want to lose
  #anything.
  def add_to_index posts
    indexDir = java.io.File.new(@index_dir);
    writer = IndexWriter.new(indexDir, StandardAnalyzer.new, !File.exists?(indexDir.absolute_path))
    posts.each do |post|
      writer.updateDocument(Term.new("id",post[:id]),create_lucene_document(post))
      @last_id = post[:id]
      @log.debug "added post ##{post[:id]}"
    end    
    writer.optimize
    writer.close
    @log.info "added #{posts.size} documents to index"
  end

  def create_lucene_document(post)
    doc = Document.new
    post.each do |key,value|
      doc.add(Field.new(key.to_s, value, Field::Store::YES, Field::Index::TOKENIZED))       
    end  
    doc
  end

end

if $0 == __FILE__  
  log = Logger.new(STDOUT)
  log.level = Logger::INFO
  index_dir = ARGV.size > 0 ? ARGV.shift : "twitter_index"
  twitter = TwitterIndexer.new index_dir
  loop do 
    begin 
      twitter.index      
    rescue StandardError => error
      log.error error
    end
  end
end
