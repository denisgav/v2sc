entity hello_world is
end;

architecture hello_world of hello_world is
begin
  stimulus : PROCESS
  begin
    assert false report "Hello World fom VHDL module"
    severity note;
    wait;
  end PROCESS stimulus;
end hello_world;
