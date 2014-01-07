
Then /^I verify app for IC venture$/ do  
  initvars
  initvars_IC
end

Then /^I choose the Ivory Coast venture$/ do
  
  performAction('click_on_text',@venture_name.to_s)
end




############################################################

