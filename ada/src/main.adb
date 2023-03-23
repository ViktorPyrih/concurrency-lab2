with Ada.Numerics.discrete_Random;
with Ada.Text_IO; use Ada.Text_IO;

procedure Main is

   Length : constant Integer := 100000;
   ThreadsCount : constant Integer := 2;
   Arr : array(1..Length) of Integer;

   function generate_random_number (from: in Integer; to: in Integer) return Integer is
       subtype Rand_Range is Integer range from .. to;
       package Rand_Int is new Ada.Numerics.Discrete_Random(Rand_Range);
       use Rand_Int;
       gen : Rand_Int.Generator;
       ret_val : Rand_Range;
   begin
      Rand_Int.Reset(gen);
      ret_val := Random(gen);
      return ret_val;
   end;

   procedure GenerateRandomArray is
      randomIndex : Integer;
      randomValue : Integer;
   begin
      for i in 1..Length loop
         Arr(i) := 0;
      end loop;

      randomIndex := generate_random_number(0, Length);
      randomValue := generate_random_number(-1000000, 0);
      Arr(randomIndex) := randomValue;

      Put_Line("");
      Put("Expected index is");
      Put(randomIndex'img);
      Put(", expected value is ");
      Put(randomValue'img);
   end GenerateRandomArray;

   task type Worker is
      entry Init(thread_index : in Integer);
   end Worker;

   protected TaskManager is
      procedure AddDoneTask(MinIndex : in Integer; MinValue : in Integer; ThreadIndex : in Integer);
      entry GetMinIndexAndValue(MinIndex : out Integer; MinValue : out Integer);
   private
      min_index : Integer;
      min_value : Integer;
      flag : Boolean := true;
      tasks_count : Integer;
   end TaskManager;

   protected body TaskManager is
      procedure AddDoneTask(MinIndex : in Integer; MinValue : in Integer; ThreadIndex : in Integer) is
      begin
         Put_Line("");
         Put("Min element in the thread");
         Put(ThreadIndex'img);
         Put(" with index");
         Put(MinIndex'img);
         Put(" is ");
         Put(MinValue'img);

         if (flag) then
            min_value := MinValue;
            min_index := MinIndex;
            flag := false;
         else
            if (MinValue < min_value) then
               min_value := MinValue;
               min_index := MinIndex;
            end if;
         end if;
         tasks_count := tasks_count + 1;
      end AddDoneTask;

      entry GetMinIndexAndValue(MinIndex : out Integer; MinValue : out Integer) when tasks_count = ThreadsCount is
      begin
         MinIndex := min_Index;
         MinValue := min_Value;
      end GetMinIndexAndValue;
   end TaskManager;

   task body Worker is
      min_index : Integer;
      min_value : Integer;
      start_index, finish_index : Integer;
      thread_index : Integer;
   begin
      accept Init(thread_index : in Integer) do
         Worker.thread_index := thread_index;
      end Init;

      start_index := ((thread_index - 1) * Length / ThreadsCount) + 1;
      finish_index := thread_index * Length / ThreadsCount;
      min_index := start_index;
      min_value := Arr(min_index);

      for i in start_index..finish_index loop
         if (Arr(i) < min_value) then
            min_index := i;
            min_value := Arr(i);
         end if;
      end loop;
      TaskManager.AddDoneTask(min_index, min_value, thread_index);
   end Worker;

   threads : array(1..ThreadsCount) of Worker;

   procedure PutResults is
      minIndex : Integer;
      minValue : Integer;
   begin
      TaskManager.GetMinIndexAndValue(minIndex, minValue);

      Put_Line("");
      Put("Actual min index is");
      Put(minIndex'img);
      Put(", actual min value is ");
      Put(minValue'img);
   end PutResults;

begin
   Put("Threads count =");
   Put(ThreadsCount'img);

   GenerateRandomArray;
   for i in 1..ThreadsCount loop
      threads(i).Init(i);
   end loop;
   PutResults;
end Main;
