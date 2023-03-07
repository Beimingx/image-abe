function flag=Sender(testimg,respath,keypath,addpath) %

%bianhao  = 15;
%caozuo=1;
%src =[testimg, num2str(bianhao) ,'.jpg'];
src=testimg;
img=imread(src);%read image
%testv2 = img;
[y1,x1,kuan,gao] = LabelBox(src); %Select the processing area，label the image location
imgEmb=img;
imgb=double(img(:,:,3));
imgg=double(img(:,:,2));
imgr=double(img(:,:,1));
w = fspecial('gaussian',[3,3],0.3); %gaussian，3*3，StandardDeviation=0.3
embeddingnum=0; 

%嵌入算法
g=double(img);   
T=1;
b=1;%秘密信息比特
cz=kuan; %关键区域宽度
cy = gao; %关键区域高度
count=30; %循环次数  循环次数和高斯滤波的滤波核系数根据照片清晰度不同需要不断调整。
con1=rand(1,cz*cy);  
con=int8(round(con1));
%econ=fliplr(con);
%执行嵌入
for a = 1 :3
    ft=zeros([count,cz,cy]);%辅助参数
    jo=zeros([count,cz,cy]);
    %控制参数定义 长度为关键区域宽度*长度
   
if(a == 1)
    g =imgb;end
if(a == 2)
    g = imgg;end
if(a == 3)
    g = imgr;end      
for p=1:count
    for i=x1+1: x1+gao
        iii=i-(x1+1)+1;
        for j= y1+1 : y1+kuan
%             disp("i=");
%             disp(i);
%             disp("j=");
%             disp(j);
            jjj= j-(y1+1)+1;
        %预测的值
        pre=predict(i+1,j+1,g);%预测
        delta=g(i+1,j+1)-double(pre); 
        f=g(i+1,j+1);
        g(i+1,j+1)=pre;
        change=0;
        for k=0:2
            for m=0:2
               change=change+(g(i+k,j+m)*w(1+k,1+m));  %滤波的值
            end
        end
        lx=fix(change)-g(i+1,j+1);
        if(delta<=T&&delta>=(-T)) 
            delta=(delta*2)+b;       
                    delta=delta+double(con(1,(i-(x1+1))*kuan+(j-(y1+1))+1));
                    yu=mod(delta,2);
                    jo(p,iii,jjj)=yu;
                    delta1=fix(delta/2);
                    if(double(pre)+delta1+lx<0||double(pre)+delta1+lx>255||delta==-1||delta==0||delta==1) %溢出
                        ft(p,iii,jjj)=1;g(i+1,j+1)=f;   
                    else
                        g(i+1,j+1)=double(pre)+delta1+lx;
                        embeddingnum=embeddingnum+1;  
                    end
        elseif(delta<(-T))
            delta=delta-T;   %-1
                 delta=delta+double(con(1,(i-(x1+1))*kuan+(j-(y1+1))+1));
                    yu=mod(delta,2);
                    jo(p,iii,jjj)=yu;
                    delta1=fix(delta/2);
                    if(double(pre)+delta1+lx<0||double(pre)+delta1+lx>255||delta==-1||delta==0||delta==1) %溢出
                        ft(p,iii,jjj)=1;g(i+1,j+1)=f;   
                    else
                        g(i+1,j+1)=double(pre)+delta1+lx;
                    end
        else  %delta>TH      
            delta=delta+T+1;
               delta=delta+double(con(1,(i-(x1+1))*kuan+(j-(y1+1))+1));
                    yu=mod(delta,2);
                    jo(p,iii,jjj)=yu;
                    delta1=fix(delta/2);
                    if(double(pre)+delta1+lx<0||double(pre)+delta1+lx>255||delta==-1||delta==0||delta==1) %溢出
                        ft(p,iii,jjj)=1;g(i+1,j+1)=f;   
                    else
                        g(i+1,j+1)=double(pre)+delta1+lx;
                    end
        end
        end
    end
end

if(a == 1)
    imgEmb(:,:,3)=g;
    ftb = ft;
    job = jo;
    %econb = econ;
end
if(a == 2)
    imgEmb(:,:,2)=g;
    ftg = ft;
    jog = jo;
    %econg = econ;
end
if(a == 3)
   imgEmb(:,:,1)=g;
   ftr = ft;
   jor = jo;
   %econr = econ;
end  
end


resimg=[respath, 'face.bmp'];  %存入文件
imwrite(uint8(imgEmb),resimg);

%subplot(1,2,1),imshow(img),title('原始图像'); 
%subplot(1,2,2),imshow(uint8(imgEmb)),title('携带秘密信息的受保护的载密图像'); 
disp("嵌入完成！");

addtion = [addpath,'face.mat'];
%save(addtion,y1,x1,kuan,gao,count,T,ftb,job,econb,ftg,jog,econg,ftr,jor,econr);
save(addtion,'y1','x1','kuan','gao','count','T','ftb','job','ftg','jog','ftr','jor');
fid =fopen(keypath,"w");
fprintf(fid,'%f\t',con1);
fclose(fid);
flag=1;
%save addrgb.mat ftb job econb ftg jog econg ftr jor econr;
% testv2=imcrop(imgEmb,[y1,x1,kuan,gao]);
% imshow(testv2);  
%imgEmb=uint8(g); 