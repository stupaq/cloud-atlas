#!/bin/sh

# cpu_load
echo "cpu_load=`top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{print (100 - $1)/100}'`"
# free_disk
free_disk_kb=`df -P --total -k | awk '/^total/{print $4}'`
echo "free_disk=`echo "$free_disk_kb*1024" | bc`"
# total_disk
total_disk_kb=`df -P --total -k | awk '/^total/{print $2}'`
echo "total_disk=`echo "$total_disk_kb*1024" | bc`"
# free_ram
echo "free_ram=`free -tb | awk '/^Mem:/{print $4}'`"
# total_ram
echo "total_ram=`free -tb | awk '/^Mem:/{print $2}'`"
# free_swap
echo "free_ram=`free -tb | awk '/^Swap:/{print $4}'`"
# total_swap
echo "total_ram=`free -tb | awk '/^Swap:/{print $2}'`"
# num_processes
echo "num_processes=`ps ax | wc -l | awk '{print $1}'`"
# num_cores
echo "num_cores=`grep -c '^processor' /proc/cpuinfo`"
# kernel_ver
echo "kernel_ver=`uname -r`"
# logged_users
echo "logged_users=`w -h | cut -d' ' -f1 | sort | uniq | wc -l`"
# dns_names
hostname -A &>/dev/null
if [[ $? -eq 0 ]]; then
  echo "dns_names=`hostname -A | sed 's/ *$//g' | tr ' ' ','`"
else
  echo "dns_names=`hostname -f | sed 's/ *$//g' | tr ' ' ','`"
fi
