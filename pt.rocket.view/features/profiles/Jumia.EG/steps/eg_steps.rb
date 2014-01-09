
Then /^I verify app for EG venture$/ do  
  initvars
  initvars_EG
end

Then /^I choose the Egypt venture$/ do
  performAction('click_on_text',@venture_name.to_s)
end




############################################################

