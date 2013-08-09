
Then /^I verify app for NG venture$/ do 
  initvars
  initvars_NG
end

Then /^I choose the Nigeria venture$/ do
  
  performAction('click_on_text',@venture_name.to_s)
end

####################################################
