package main

import (
	"fmt"
	"os"
	"os/exec"
)

func main() {
	targets := []string{"docker", "rancher", "colima", "podman"}
	for _, target := range targets {
		execute(target, 20)
	}
}

func execute(target string, times int) {
	for i := 0; i < times; i++ {
		cmd := exec.Command("make", target)
		cmd.Stdout = os.Stdout
		cmd.Stderr = os.Stderr
		err := cmd.Run()
		if err != nil {
			fmt.Println(err)
		}
		cmd.Wait()
	}
}
