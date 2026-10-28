-- Нагружает процессор. Без кулера ПК должен перегреться и выключиться.
local computer = require("computer")
print("Нагрузка запущена, Ctrl+Alt+C для остановки")
while true do
  local x = 0
  for i = 1, 200000 do x = x + math.sqrt(i) end
  os.sleep(0)
end
