
Then /^I verify app for MA venture$/ do  
  initvars
  initvars_MA
end

#####################################################

Then /^I touch a valid restaurant in the CO venture$/ do
  
  performAction('click_on_text',@restaurant_CO.to_s)
end

Then /^I touch a valid category in the CO venture$/ do
  
  performAction('click_on_text',@category_CO.to_s)
end

Then /^I touch a valid product in the CO venture$/ do
  
  performAction('click_on_text',@product_CO.to_s)
end

Then /^I touch a valid variation in the CO venture$/ do
  
  performAction('click_on_text',@variation_CO.to_s)
end

Then /^I touch a valid search in the CO venture$/ do
  
  performAction('click_on_text',@search_CO.to_s)
end

Then /^I enter a valid CO hint into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@hint_CO.to_s, name)

end