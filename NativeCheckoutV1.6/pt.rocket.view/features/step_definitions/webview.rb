#
# WEBVIEWS (NEEDS CALABASH-ANDROID-0.3.8)
#

Then /^I scroll to next button$/ do 
  performAction('scroll_to','css','button[id=billingbtn]')
# performAction('execute_javascript',"(document.getElementById('<billingbtn>')).scrollIntoView(true)")
# performAction('scroll_down')
end

Then /^I proceed to the next step of the checkout staging$/ do 
  performAction('touch','webView','button[id=billingbtn]')
  performAction('touch','webView','button[id=billingbtn]')
# touch("webview css:'a:contains(“^Next$”)")
# performAction("touch", 'css', 'a[text=Next]')
# touch("webview css:'a[id=paymentmethodbtn]'")
# touch %Q{webView css:'button[id="billingbtn"]'}
# performAction("dump_html")
end

Then /^I choose the COD payment method$/ do 
  performAction('touch','webView','label[for=cashondelivery]')
  performAction('scroll_to','css','button[id=paymentbtn]')
# performAction("dump_html")
end

Then /^I choose payment method$/ do 
  performAction('touch','webView','button[id=paymentbtn]')
  performAction('touch','webView','button[id=paymentbtn]')
# performAction("dump_html")
end

Then /^I confirm the order$/ do 
  performAction('scroll_to','css','button[id=confirmbtn]')
  performAction('touch','webView','button[id=confirmbtn]')
  performAction('touch','webView','button[id=confirmbtn]')
# performAction('click_on_text',@checkout3.to_s)
end

Then /^I press the button to continue shopping$/ do 
  performAction('press',@continueshopping.to_s)
end