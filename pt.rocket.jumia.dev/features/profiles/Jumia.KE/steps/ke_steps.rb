
Then /^I verify app for KE venture$/ do 
  initvars
  initvars_KE
end



Then /^I choose the Kenya venture$/ do
  
  performAction('click_on_text',@venture_name.to_s)
end





#########################################################################


