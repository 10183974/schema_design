clear all;
clc;
close all;

size = [30,60,90,120,150];
col = ['r','b','g','c','y'];
mar = ['+','s','d','.','*'];

rrse_small = zeros(3,5);
rrse_large = zeros(3,5);

%====join
k = 1;
figure;
y0 = load('zjoin_testRealLatency.txt');
y0 = y0((1:5:end));
x = 1:length(y0);
plot(x,y0,'-ko');
hold on;

for i = 1:5
    str = ['zjoin_',num2str(size(i)),'_testEstimateLatency.txt'];
    y = load(str);
    y = y(1:5:end);
    plot(x,y,'color',col(i),'marker',mar(i));
   
    bound = 2E3;
    ind_small = find(y0<bound);
    rrse_small(k,i) = sum(abs(y(ind_small)-y0(ind_small)))/sum(y0(ind_small));
    
    ind_large = find(y0>bound);
    rrse_large(k,i) = sum(abs(y(ind_large)-y0(ind_large)))/sum(y0(ind_large));
      
    disp([size(i),rrse_small(k,i),rrse_large(k,i)]);
    
    hold on;
    
    
end
hold off;
grid minor;
title('Latency Estimation of Join Query','FontSize',16);
xlabel('Test case','FontSize',14);
ylabel('latency (ms)','FontSize',14);
legend('real latency','estimate with 30','estimate with 60','estimate with 90','estimate with 120','estimate with 150');
set(gca,'FontSize',16);
saveas(gca,'join.jpg');




%====scan
k = 2;
figure;
y0 = load('zscan_testRealLatency.txt');
y0 = y0((1:1:end));
x = 1:length(y0);
plot(x,y0,'-ko');
hold on;

for i = 1:5
    str = ['zscan_',num2str(size(i)),'_testEstimateLatency.txt'];
    y = load(str);
    y = y(1:1:end);
    plot(x,y,'color',col(i),'marker',mar(i));
   
    bound = 2E3;
    ind_small = find(y0<bound);
    rrse_small(k,i) = sum(abs(y(ind_small)-y0(ind_small)))/sum(y0(ind_small));
    ind_large = find(y0>bound);
    rrse_large(k,i) = sum(abs(y(ind_large)-y0(ind_large)))/sum(y0(ind_large));     
    disp([size(i),rrse_small(k,i),rrse_large(k,i)]);   
    hold on;
       
end
hold off;
grid minor;
title('Latency Estimation of Scan Query','FontSize',16);
xlabel('Test case','FontSize',14);
ylabel('latency (ms)','FontSize',14);
legend('real latency','estimate with 30','estimate with 60','estimate with 90','estimate with 120','estimate with 150');
set(gca,'FontSize',16);
saveas(gca,'scan.jpg');

%====read
k = 3;
figure;
y0 = load('zread_testRealLatency.txt');
y0 = y0((1:5:end));
x = 1:length(y0);
plot(x,y0,'-ko');
hold on;

for i = 1:5
    str = ['zread_',num2str(size(i)),'_testEstimateLatency.txt'];
    y = load(str);
    y = y(1:5:end);
    plot(x,y,'color',col(i),'marker',mar(i));
   
    bound = 2E3;
    ind_small = find(y0<bound);
    rrse_small(k,i) = sum(abs(y(ind_small)-y0(ind_small)))/sum(y0(ind_small));
    ind_large = find(y0>bound);
    rrse_large(k,i) = sum(abs(y(ind_large)-y0(ind_large)))/sum(y0(ind_large));     
    disp([size(i),rrse_small(k,i),rrse_large(k,i)]);   
    hold on;
       
end
hold off;
grid minor;
title('Latency Estimation of Read Query','FontSize',16);
xlabel('Test case','FontSize',14);
ylabel('latency (ms)','FontSize',14);
legend('real latency','estimate with 30','estimate with 60','estimate with 90','estimate with 120','estimate with 150');
set(gca,'FontSize',16);
saveas(gca,'read.jpg');

% 
figure;
plot(size,rrse_small(1,:),'-ko');
hold on;
plot(size,rrse_small(2,:),'-r*');
plot(size,rrse_small(3,:),'-bs');
grid minor;
title('Relative Absolute Error of Small Latency < 2000 ms','FontSize',16);
xlabel('Sample size','FontSize',14);
ylabel('Error','FontSize',14);
legend('Join Query','Scan Query','Read Query');
set(gca,'FontSize',16);
hold off;
saveas(gca,'smallError.jpg');


% 
figure;
plot(size,rrse_large(1,:),'-ko');
hold on;
plot(size,rrse_large(2,:),'-r*');
plot(size,rrse_large(3,:),'-bs');
grid minor;
title('Relative Absolute Error of Small Latency < 2000 ms','FontSize',16);
xlabel('Sample size','FontSize',14);
ylabel('Error','FontSize',14);
legend('Join Query','Scan Query','Read Query');
set(gca,'FontSize',16);

hold off;
saveas(gca,'largeError.jpg');
% 




