-- Монитор температуры для OpenComputers (нужен установленный кулер)
local component = require("component")
local event = require("event")

if not component.isAvailable("cooler") then
  print("Кулер не установлен!")
  return
end
local cooler = component.cooler
print("Airflow: " .. cooler.getAirflow() .. " CFM")
print("Ctrl+C для выхода")
while true do
  local t = cooler.getTemperature()
  print(string.format("Температура: %.1f C", t))
  if t >= 90 then print("!!! ТРОТТЛИНГ !!!") end
  if event.pull(1, "interrupted") then break end
end
