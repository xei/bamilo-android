require 'calabash-android/management/adb'
require 'calabash-android/operations'

Before do |scenario|
  
  uninstall_apps
  install_app(ENV["TEST_APP_PATH"])
  install_app(ENV["APP_PATH"])
  start_test_server_in_background
end

After do |scenario|
  if scenario.failed?
    #screenshot_embed
  end
  shutdown_test_server
end
