
Then /^I verify app for KE venture$/ do 
  initvars
  initvars_KE
end





#########################################################################




Then /^I touch a valid restaurant in the KE venture$/ do
  
  performAction('click_on_text',@restaurant_KE.to_s)
end

Then /^I touch a valid category in the KE venture$/ do
  
  performAction('click_on_text',@category_KE.to_s)
end

Then /^I touch a valid product in the KE venture$/ do
  
  performAction('click_on_text',@product_KE.to_s)
end

Then /^I touch a valid variation in the KE venture$/ do
  
  performAction('click_on_text',@variation_KE.to_s)
end

Then /^I touch a valid search in the KE venture$/ do
  
  performAction('click_on_text',@search_KE.to_s)
end

Then /^I enter a valid KE hint into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@hint_KE.to_s, name)

end