
Then /^I verify app for MA venture$/ do  
  initvars
  initvars_MA
end

Then /^I choose the Morocco venture$/ do
  performAction('click_on_text',@venture_name.to_s)
end




############################################################

