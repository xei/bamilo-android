require 'nokogiri'

res_path = "JumiaApp/src/main/res"

res_configs = ["fr", "pt", "my", "ar", "fa", "ur"]
str_filename = "strings.xml"
files_translated_path = res_configs.map { |config| res_path + "/values-" + config + "/" + str_filename }

file_base_path = res_path + "/values/strings.xml"
file_base = File.new(file_base_path)
doc_base = Nokogiri::XML(file_base)
@count = 0

puts "
########################################
# SCRIPT USED TO VALIDATE PLACEHOLDERS #
########################################
"

def check_placeholder_count(str_name, str_base, str_translated, placeholder)
	if str_base.text.scan(placeholder).length != str_translated.text.scan(placeholder).length
	then
		puts str_name +  " | " + str_base.text + " | " + str_translated.text
		@count += 1
	end
end	

files_translated_path.each do |file_translated_path|
	puts "\n\n"
	puts file_translated_path

	doc_translated_path = File.new(file_translated_path)
	doc_translated = Nokogiri::XML(doc_translated_path)

	doc_translated.search('//string').each do |str_translated|

		str_name = str_translated.attribute("name").text
		str_base = doc_base.search("//string[@name=\"#{str_name}\"]")

		check_placeholder_count(str_name, str_base, str_translated, "%d")
		check_placeholder_count(str_name, str_base, str_translated, "%s")
		check_placeholder_count(str_name, str_base, str_translated, "%1$s")
		check_placeholder_count(str_name, str_base, str_translated, "%2$s")
		
	end

end

puts @count

