
Then /^I verify app for NG venture$/ do 
  initvars
  initvars_NG
end


####################################################

Then /^I touch a valid restaurant in the CI venture$/ do
  
  performAction('click_on_text',@restaurant_CI.to_s)
end

Then /^I touch a valid category in the CI venture$/ do
  
  performAction('click_on_text',@category_CI.to_s)
end

Then /^I touch a valid product in the CI venture$/ do
  
  performAction('click_on_text',@product_CI.to_s)
end

Then /^I touch a valid search in the CI venture$/ do
  
  performAction('click_on_text',@search_CI.to_s)
end

Then /^I enter a valid CI hint into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@hint_CI.to_s, name)

end