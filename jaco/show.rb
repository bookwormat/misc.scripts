scores = []
best_score = nil
best_file = nil

Dir.entries("logs").each do |file|
  if file =~ /mebp-12.*log$/
    score = File.new("logs/#{file}").readlines[-4].split(':')[1].strip.to_f
    scores << score
    if best_score.nil? || score < best_score
      best_score = score
      best_file = file
    end
    puts score
  end
end

mean = scores.inject(:+) / scores.size.to_f
stddev = Math.sqrt( scores.inject(0) { |sum, e| sum + (e - mean) ** 2 } / scores.size.to_f )


puts "best file is #{best_file}, score=#{best_score}, mean=#{mean}, stddev="+ "%.4f"%(stddev/mean)

