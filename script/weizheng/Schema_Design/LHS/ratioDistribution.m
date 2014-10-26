clear all;
close all;
clc;

A = unifrnd(1000,1E4,1,1E6);
B = unifrnd(1E4,1E6,1,1E6);
hist(B./A,50);
title('Distribution of Ratio of Two Uniform Random Variable','FontSize',18);
xlabel('O_{#row} / C_{#row}','FontSize',14);
ylabel('Counts','FontSize',14);
set(gca,'FontSize',14);
