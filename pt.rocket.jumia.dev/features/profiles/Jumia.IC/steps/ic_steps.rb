
Then /^I verify app for IC venture$/ do  
  initvars
  initvars_IC
end


############################################################

Then /^I touch a valid restaurant in the CL venture$/ do
  
  performAction('click_on_text',@restaurant_CL.to_s)
end

Then /^I touch a valid category in the CL venture$/ do
  
  performAction('click_on_text',@category_CL.to_s)
end
       
Then /^I touch a valid product in the CL venture$/ do
  performAction('click_on_text',@product_CL.to_s)
end

Then /^I touch a valid variation in the CL venture$/ do
  
  performAction('click_on_text',@variation_CL.to_s)
end

Then /^I touch a valid search in the CL venture$/ do
  
  performAction('click_on_text',@search_CL.to_s)
end


Then /^I enter a valid CL hint into "([^\"]*)"$/ do |name|
  
  performAction('enter_text_into_named_field',@hint_CL.to_s, name)

end