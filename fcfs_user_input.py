

processes = []

n = int(input("Enter number of processes: "))

for i in range(n):
    pid = input(f"\nEnter Process ID for process {i+1}: ")
    arrival = int(input("Enter Arrival Time: "))
    burst = int(input("Enter Burst (Service) Time: "))
    processes.append({"pid": pid, "arrival": arrival, "burst": burst})

processes.sort(key=lambda x: x["arrival"])

current_time = 0
for p in processes:
    if current_time < p["arrival"]:
        current_time = p["arrival"]
    p["start"] = current_time
    p["completion"] = p["start"] + p["burst"]
    p["turnaround"] = p["completion"] - p["arrival"]
    p["waiting"] = p["turnaround"] - p["burst"]
    current_time = p["completion"]

print("\n--- FCFS CPU Scheduling ---\n")
print("{:<5} {:<10} {:<10} {:<10} {:<12} {:<12} {:<10}".format(
    "PID", "Arrival", "Burst", "Start", "Completion", "Turnaround", "Waiting"
))

for p in processes:
    print("{:<5} {:<10} {:<10} {:<10} {:<12} {:<12} {:<10}".format(
        p["pid"], p["arrival"], p["burst"], p["start"],
        p["completion"], p["turnaround"], p["waiting"]
    ))

avg_tat = sum(p["turnaround"] for p in processes) / n
avg_wt = sum(p["waiting"] for p in processes) / n

print("\nAverage Turnaround Time:", round(avg_tat, 2))
print("Average Waiting Time:", round(avg_wt, 2))
